<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Prijava Korisnika</title>
    </head>
    <body>
        <h1>Prijava korisnika</h1>
        <div>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/registracija">Registracija korisnika</a></li>
        </div>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/prijava">
            <table>
                <tr>
                    <td>Korisničko ime:</td>
                    <td><input type="text" name="korime" /></td>
                </tr>
                <tr>
                    <td>Lozinka:</td>
                    <td><input type="password" name="lozinka" /></td>
                </tr>
                <tr>
                    <td></td>
                    <td><input type="submit" value="Prijavi me" /></td>
                </tr>
            </table>
        </form>
        <c:if test="${requestScope.greska!=null}">
            <p>${requestScope.greska}</p>
        </c:if>
    </body>
</html>
