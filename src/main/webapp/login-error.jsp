<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%
response.setHeader("Refresh", "3;url=login.jsp?error=true"); %>
<!DOCTYPE html>
<html>
	<head>
		<title>Login Failed - Student Submission System</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link
			href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
			rel="stylesheet"
		/>
		<style>
			body {
				background-color: #f8f9fa;
			}
			.error-container {
				max-width: 400px;
				margin: 100px auto;
				padding: 20px;
				background-color: white;
				border-radius: 5px;
				box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
				text-align: center;
			}
			.error-icon {
				color: #dc3545;
				font-size: 48px;
				margin-bottom: 20px;
			}
			.error-message {
				color: #333;
				margin-bottom: 20px;
			}
			.redirect-message {
				color: #6c757d;
				font-size: 14px;
			}
		</style>
	</head>
	<body>
		<div class="container">
			<div class="error-container">
				<div class="error-icon">&#x2716;</div>
				<h2 class="error-message">Login Failed</h2>
				<p>Invalid email or password. Please try again.</p>
				<p class="redirect-message">
					You will be redirected to the login page in 3 seconds...
					<br />
					<a href="login.jsp">Click here</a> if you are not redirected
					automatically.
				</p>
			</div>
		</div>

		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
	</body>
</html>
