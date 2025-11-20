<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Ошибка</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link rel="stylesheet" href="css/default.css">
    <link rel="stylesheet" href="css/styles.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100..900;1,100..900&display=swap"
          rel="stylesheet">
</head>
<body>
<div class="page-center-wrapper">
    <div class="page-center">
        <p class="page-header">Произошла ошибка!</p>
        <div class="main-block">
            <div class="error-details">
                <%
                    // Этот скриплет найдет "самую глубокую" причину ошибки
                    Throwable rootCause = exception;
                    while (rootCause.getCause() != null) {
                        rootCause = rootCause.getCause();
                    }
                    request.setAttribute("rootCause", rootCause);
                %>

                <p>
                    <strong>Код ошибки:</strong> ${requestScope['javax.servlet.error.status_code']}
                </p>
                <p>
                    <strong>Сообщение об ошибке:</strong> ${rootCause.message}
                </p>
                <hr>
                <p>
                    Произошла непредвиденная ошибка при обработке вашего запроса. Вы можете вернуться на главную страницу, попробовав снова.
                </p>
            </div>
        </div>
    </div>
</div>
<div class="go-home">
    <a href="${pageContext.request.contextPath}" title="На главную">
        <img src="images/home.svg" alt="Домой">
    </a>
</div>
</body>
</html>