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
    <td class="navigation" width="20%" align="left" nowrap>
      <!-- Link to parent index, if any -->
      <a href="../index.html"><img SRC="../../res/up.gif" border=0 alt="Up one level"></a>
      <!-- Create navigation buttons if more than one index page -->
    </td>

    <td align="center" nowrap>
      <h1>Directory: <c:out value="${album.title}"/> (<c:out value="${path}"/>)</h1>
    </td>
    <td class="number" width="10%" align="right" nowrap/>
  </tr>
</table>


<!-- Subdirectories -->
<c:if test="${album.numSubdirectories > 0}">
  <h2>Subdirectories:</h2>
  <table align="left">
    <c:forEach var="subdirectory" items="${album.subdirectories}">
      <tr>
        <td valign="bottom" width="160">
          <a href="<c:url value='/do/index'><c:param name='path' value='${path}/${subdirectory.name}'/></c:url>">
            <c:out value="${subdirectory.title}"/>
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
        <a href="<c:url value='/do/picture'><c:param name='path' value='${path}'/><c:param name='name' value='${picture.name}'/></c:url>">
          <img class="image" border="0" width="160" height="120"
            src="<c:url value='/do/view-thumbnail'><c:param name='path' value='${path}'/><c:param name='name' value='${picture.name}'/></c:url>"
          >
        </a>
        <br>
        <small><c:out value="${picture.name}"/> <c:out value="${picture.dateTimeString}"/></small>
      </td>
      <c:set var="i" value="${i+1}"/>
      <c:if test="${((i mod 6) == 0) and (i > 0)}"></tr><tr></c:if>
    </c:forEach>
    </tr>
  </table>
</c:if>

</body>
</html>
