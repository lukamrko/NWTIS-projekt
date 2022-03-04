<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Podrucja</title>
    </head>
    <body>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/pocetna">Početna</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/aerodrom">Aerodromi</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/upis">Slobodni upis komande</a></li>
        </ul>
        <h1>Podrucja:</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/podrucja">
            <label for="korisnici">Izaberi korisnika:</label>
            <select name="korisnik" id="korisnik">
                <c:forEach var="k" items="${requestScope.korisnici}">
                    <option value="${k.getKorime()}">${k.getKorime()}</option>
                </c:forEach>
            </select>
            <br><br>
            <label for="podrucje">Izaberi podrucja:</label>
            <select name="podrucje" id="podrucje">
                <c:forEach var="p" items="${requestScope.podrucja}">
                    <option value="${p}">${p}</option>
                </c:forEach>
            </select>
            <br><br>
            <label for="aktivacija">Stanje područja:</label>
            <select name="aktivacija" id="aktivacija">
                <option value="aktiviraj" >aktiviraj</option>
                <option value="deaktiviraj">deaktiviraj</option>
            </select>
            <br><br>
            <input type="submit" value="Ažuriraj područje" />
        </form>
        <c:if test="${requestScope.info!=null}">
            <p>${requestScope.info}</p>
        </c:if>
    </body>
</html>
