<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Upis komande</title>
    </head>
    <body>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/pocetna">Početna</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/podrucja">Dodjela podrucja</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/aerodrom">Pregled aerodroma</a></li>
        </ul>
        <h1>Upis komande:</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/upis">
            <table>
                <tr>
                    <td>Komanda:</td>
                    <td><input type="text" name="komanda" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="Posalji komandu" /></td>
                </tr>
            </table>
        </form>
        <c:if test="${requestScope.greska!=null}">
            <p>${requestScope.greska}</p>
        </c:if>
    </body>
</html>
