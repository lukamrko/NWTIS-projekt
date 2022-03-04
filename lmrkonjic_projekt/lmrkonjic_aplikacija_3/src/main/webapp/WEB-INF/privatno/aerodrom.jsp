<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Aerodromi</title>
    </head>
    <body>
        <ul>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/pocetna">Početna</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/podrucja">Dodjela podrucja</a></li>
            <li><a href="${pageContext.servletContext.contextPath}/mvc/upis">Slobodni upis komande</a></li>
        </ul>
        <h1>Aerodromi</h1>
        <form method="POST" action="${pageContext.servletContext.contextPath}/mvc/aerodrom">
            <label for="icao">Aerodrom za dodati:</label>
            <select name="icao" id="icao">
                <c:forEach var="i" items="${requestScope.icaoDodati}">
                    <option value="${i.getIcao()}">${i.getIcao()} - ${i.getNaziv()} </option>
                </c:forEach>
            </select>
            <br><br>
            <label for="aerodrom">Aerodromi:</label>
            <select name="aerodrom" id="aerodrom" size="10">
                <c:forEach var="a" items="${requestScope.aerodromi}">
                    <option value="${a.getIcao()}">${a.getIcao()} - ${a.getNaziv()} </option>
                </c:forEach>
            </select>
            <br><br>
            <button type="submit" name="vrsta" value="prikazi">Prikazi korisnike za aerodrom</button>
            <button type="submit" name="vrsta" value="prestani">Prestani pratiti aerodrom</button>
            <button type="submit" name="vrsta" value="dodaj">Dodaj aerodrom za pratiti</button>
        </form>
        <ul>
            <c:forEach var="k" items="${requestScope.korisnici}">
                <li>${k.getKorime()} - ${k.getPrezime()} - ${k.getIme()} </li>
                </c:forEach>
        </ul>
        <c:if test="${requestScope.info!=null}">
            <p>${requestScope.info}</p>
        </c:if>
    </body>
</html>
