<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Новый матч</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link rel="stylesheet" href="css/default.css">
    <link rel="stylesheet" href="css/styles.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">
</head>
<body>
<div class="page-center-wrapper">
    <div class="page-center">
        <p class="page-header">Новый матч</p>
        <% String error = (String) request.getAttribute("error"); %>
        <% if (error != null) { %>
        <p class="error"><%= error %></p>
        <% } %>
        <div class="main-block">
            <form method="POST" action="new-match" accept-charset="UTF-8">

                <div class="form-element-wrapper">
                    <label for="first-player-name">Игрок №1</label>
                    <input type="text" name="first-player-name" id="first-player-name" placeholder="Введите имя">
                </div>
                <div class="form-element-wrapper">
                    <label for="second-player-name">Игрок №2</label>
                    <input type="text" name="second-player-name" id="second-player-name" placeholder="Введите имя">
                </div>
                <div class="form-element-wrapper">
                    <button class="start-button" type="submit">Начать матч</button>
                </div>
            </form>
        </div>
    </div>
</div>
<div class="go-home">
    <a href="${pageContext.request.contextPath}"><img src="images/home.svg"></a>
</div>
</body>
</html>