<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="org.podval.album.Picture" %>

<jsp:useBean id="picture" scope="request" type="org.podval.album.Picture"/>


<html>
  <head>
    <title><c:out value="${picture.album.name}"/></title>
    <link href="../../res/styles.css" rel=stylesheet>
    <meta name="Generator" content="Podval photo album by Leonid Dubinsky, www.podval.org">
  </head>


<body id="index" bgcolor="white">

<table align="center" width="90%">
  <tr class="head">
    <td align="left" class="navigation" width="20%" nowrap>
      <!-- Links to prev & next... -->
    </td>

    <td align="center" nowrap>
      <h2><c:out value="${album.title}"/> (<c:out value="${picture.album.path}"/>)</h2>
    </td>
  </tr>
</table>

<a href="<c:url value='/do/original'><c:param name='path' value='${picture.path}'/></c:url>">
  <img
    class="image"
    src="<c:url value='/do/view-screensized'><c:param name='path' value='${picture.path}'/></c:url>"
    border="0"
  >
  <br>
  <h3><c:out value="${picture.name}"/></h3>
</a>

</body>
</html>
