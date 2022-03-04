<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Početna stranica prijavljenog korisnika</title>
    </head>
    <body>
        <h1>Početna stranica prijavljenog korisnika</h1>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/podrucja">Dodjela podrucja</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/aerodrom">Pregled aerodroma</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/upis">Slobodni upis komande</a></li>
        </ul>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/pocetna">
            <table>
                <tr>
                    <td><input type="submit" value="Odjavi me" /></td>
                </tr>
            </table>
        </form>
    </body>
</html>
