<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<%@ page import="org.podval.album.Picture" %>

<jsp:useBean id="picture" scope="request" type="org.podval.album.Picture"/>


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
      <h1><c:out value="${album.title}"/> (<c:out value="${path}"/>)</h1>
		</td>

		<td class="number" width="10%" align="right" nowrap>
		</td>
	</tr>
</table>

<a href="<c:url value='/do/original'><c:param name='path' value='${path}'/><c:param name='name' value='${picture.name}'/></c:url>">
  <img
    class="image"
    src="<c:url value='/do/view-screensized'><c:param name='path' value='${path}'/><c:param name='name' value='${picture.name}'/></c:url>"
    width="640"
    height="480"
    border="0"
  >
  <br>
  <small><c:out value="${picture.name}"/></small>
</a>

</body>
</html>
