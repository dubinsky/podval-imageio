<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="org.podval.album.Album" %>

<jsp:useBean id="album" scope="request" type="org.podval.album.Album"/>


<html>
  <head>
    <title><c:out value="${album.title}"/></title>
    <link href="../../res/styles.css" rel=stylesheet>
    <meta name="Generator" content="Podval photo album by Leonid Dubinsky, www.podval.org">
  </head>


<body id="index" bgcolor="white">

<table align="center" width="90%">
  <tr class="head">
    <td align="left" class="navigation" width="20%" nowrap>
      <c:if test="${album.parent != null}">
        <a href="<c:url value='/do/index'><c:param name='path' value='${album.parent.path}'/></c:url>">
          <img src="../../res/up.gif" border=0 alt="Parent">
        </a>
      </c:if>
    </td>

    <td align="center" nowrap>
      <h2><c:out value="${album.title}"/> (<c:out value="${album.path}"/>)</h2>
    </td>
  </tr>
</table>


<!-- Subdirectories -->
<c:if test="${album.numSubalbums > 0}">
  <h3>Subalbums:</h3>
  <table align="left">
    <c:forEach var="subalbum" items="${album.subalbums}">
      <tr>
        <td valign="bottom" width="160">
          <a href="<c:url value='/do/index'><c:param name='path' value='${subalbum.path}'/></c:url>">
            <c:out value="${subalbum.title}"/>
          </a>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:if>

<!-- Pictures -->
<c:if test="${album.numPictures > 0}">
  <table align="center">
    <tr>
    <c:set var="i" value="0"/>
    <c:forEach var="picture" items="${album.pictures}">
      <td valign="bottom" width="160">
        <a href="<c:url value='/do/picture'><c:param name='path' value='${picture.path}'/></c:url>">
          <img class="image" border="0" width="160" height="120"
            src="<c:url value='/do/view-thumbnail'><c:param name='path' value='${picture.path}'/></c:url>"
          >
        </a>
        <br>
        <small><c:out value="${picture.name}"/></small>
        <br>
        <small><c:out value="${picture.dateTimeString}"/></small>
      </td>
      <c:set var="i" value="${i+1}"/>
      <c:if test="${((i mod 6) == 0) and (i > 0)}"></tr><tr></c:if>
    </c:forEach>
    </tr>
  </table>
</c:if>

</body>
</html>
