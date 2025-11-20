<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java"%>
<%--<%@ taglib uri="jakarta.tags.core" prefix="c" %>--%>
<%--<%@ taglib uri="http://xmlns.jcp.org/jsp/jstl/core" prefix="c" %>--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <title>Итоги матчей</title>
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
        <p class="page-header">Итоги матчей</p>
        <div class="main-block">
            <div class="player-filter-form-wrapper">
                <form class="filter-form" method="POST" action="matches" accept-charset="UTF-8">
                    <label for="filter-by-player-name">Поиск по имени игрока:</label>
                    <input type="text" name="filter-by-player-name" id="filter-by-player-name"
                           placeholder="Введите имя">
                    <button class="search-button" type="submit">Найти</button>
                </form>
            </div>
            <div class="scores">
                <c:choose>
                    <c:when test="${not empty matches}">
                        <table class="scores-table">
                            <tr>
                                <th>№ Матча</th>
                                <th>Игрок №1</th>
                                <th>Игрок №2</th>
                                <th>Счет</th>
                                <th>Победитель</th>
                            </tr>
                            <c:forEach var="match" items="${matches}">
                                <tr>
                                    <td>${match.id}</td>
                                    <td>${match.firstPlayer.name}</td>
                                    <td>${match.secondPlayer.name}</td>
                                    <td>${match.score.score}</td>
                                    <td>${match.winner.name}</td>
                                </tr>
                            </c:forEach>
                        </table>

                    </c:when>
                    <c:otherwise>
                        <div class="matches-not-found-message">
                            <p>Матчи не найдены</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <nav>
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="matches?page=${currentPage - 1}">Назад</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span>${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="matches?page=${i}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="matches?page=${currentPage + 1}">Вперед</a>
                    </c:if>
                </div>
            </nav>

        </div>
    </div>
</div>
<div class="go-home">
    <a href="${pageContext.request.contextPath}"><img src="images/home.svg"></a>
</div>
</body>
</html>