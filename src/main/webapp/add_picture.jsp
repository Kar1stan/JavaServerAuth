<%@ page contentType="text/html;charset=UTF-8"  %>
<div>
    Добавить новое изображение в галерею
    <form method="post" action="add-picture" enctype="multipart/form-data">
        <label>Изображение: <input type="file" name="pictureFile" /></label>
        <br/>
        <label>Описание: <textarea name="pictureDescription">Вот это да!!</textarea></label>
        <br/>
        <button>Добавить</button>
    </form>
</div>
