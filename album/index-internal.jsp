<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="org.podval.album.PictureDirectory" %>

<jsp:useBean id="directory" scope="request" type="org.podval.album.PictureDirectory"/>


<html>
  <head>
    <title><c:out value="${directory.name}"/></title>
    <link href="../../res/styles.css" rel=stylesheet>
    <meta name="Generator" content="Podval photo album by Leonid Dubinsky, www.podval.org">
  </head>


<body id="index" bgcolor="white">

<table align="center" width="90%">
	<tr class="head">
		<td class="navigation" width="20%" align="left" nowrap>
		  <!-- Link to parent index, if any -->
			<a href="../index.html"><img SRC="../../res/up.gif" BORDER=0 ALT="Up one level"></a>

			<!-- Create navigation buttons if more than one index page -->
	 </td>

		<td align="center" nowrap>
      <h1><c:out value="${directory.name}"/> (<c:out value="${path}"/>)</h1>
		</td>

		<td class="number" width="10%" align="right" nowrap>
		</td>
	</tr>
</table>


<!-- Subdirectories -->
<c:if test="${directory.numSubdirectories > 0}">
  <table align="center">
    <tr>
    <c:set var="i" value="0"/>
    <c:forEach var="subdirectory" items="${directory.subdirectories}">
      <td valign="bottom" width="160">
        <a href="<c:url value='/do/index'><c:param name='path' value='${path}/${subdirectory.name}'/></c:url>">
          <img class="image" src="" width="160" height="120" border="0"><br>
          <small><c:out value="${subdirectory.name}"/></small>
        </a>
      </td>
      <c:set var="i" value="${i+1}"/>
      <c:if test="${((i mod 6) == 0) and (i > 0)}"></tr><tr></c:if>
    </c:forEach>
    </tr>
  </table>
</c:if>

<!-- Pictures -->
<c:if test="${directory.numPictures > 0}">
  <table align="center">
    <tr>
    <c:set var="i" value="0"/>
    <c:forEach var="picture" items="${directory.pictures}">
      <td valign="bottom" width="160">
        <a href="<c:url value='/do/picture'><c:param name='path' value='${path}'/><c:param name='name' value='${picture.name}'/></c:url>">
          <img
            class="image"
            src="<c:url value='/do/view'><c:param name='path' value='${path}'/><c:param name='name' value='${picture.name}'/><c:param name='view' value='thumbnail'/></c:url>"
            width="160"
            height="120"
            border="0"
					>
          <br>
          <small><c:out value="${picture.name}"/></small>
        </a>
      </td>
      <c:set var="i" value="${i+1}"/>
      <c:if test="${((i mod 6) == 0) and (i > 0)}"></tr><tr></c:if>
    </c:forEach>
    </tr>
  </table>
</c:if>

</body>
</html>