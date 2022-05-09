package com.example.sqljavaredirectserver.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.example.sqljavaredirectserver.dao.PictureDao;
import com.example.sqljavaredirectserver.orm.Picture;
import com.example.sqljavaredirectserver.orm.User;
import com.example.sqljavaredirectserver.services.Hasher;
import com.example.sqljavaredirectserver.services.RndService;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@WebServlet("/add-picture")
@MultipartConfig()
@Singleton
public class AddPictureServlet extends HttpServlet {

    @Inject Hasher     hasher ;
    @Inject RndService rnd;
    @Inject PictureDao pictureDao;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //
        Part pic = req.getPart( "pictureFile" ) ;
        String uploadPathStr = req.getServletContext().getRealPath(".") +
                File.separator +
                ".." + File.separator +
                ".." + File.separator +
                "uploads" + File.separator;

        // задача: отделяем расширение, генерируем хеш из имени файла и метки времени
        // состыковываем с расширением, сохраняем (если файл с таким именем есть,
        // повторяем хеширование). Вносим имя сохраненного файла и описание в БД
        // политика безопасности - загружаем только изображения PNG, JPG/JPEG

        // имя загруженного файла
        String uploadedFilename = pic.getSubmittedFileName() ;
        // определяем расширение
        int dotPos = uploadedFilename.lastIndexOf( '.' ) ;
        if( dotPos == -1 ) {  // в имени файла нет точки - такие не принимаем
            // записываем в сессию статус - ошибка и завершаем
        }
        String ext = uploadedFilename.substring( dotPos ) ;

        // генерируем имя и убеждаемся, что такого имени нет в папке
        String savedFilename = uploadedFilename;
        File savedFile;
        do {
            savedFilename = hasher.hash( savedFilename + rnd.getRandom() ) + ext ;
            savedFile = new File( uploadPathStr + savedFilename ) ;
        } while( savedFile.isFile() ) ;
        Files.copy(
                pic.getInputStream(),
                savedFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        ) ;
        // Д.З. - скопировать файл под новым именем в папку загрузки
        // убедиться что он попадает в папку и БД (детали - в AddPictureServlet)

        // передаем в БД файл и описание
        pictureDao.addPicture(
                new Picture(
                        req.getParameter( "pictureDescription" ),
                        savedFilename,
                        ((User)req.getAttribute("user")).getId()
                        )
        ) ;

        System.out.println("AddPictureServlet works " +
                hasher.hash( uploadPathStr + pic.getSubmittedFileName() )
                + ext
        ) ;

        resp.sendRedirect( req.getContextPath() ) ;
    }
}
