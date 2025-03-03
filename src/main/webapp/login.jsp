<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
	<head>
		<title>Teacher Login - Student Submission System</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<link
			href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
			rel="stylesheet"
		/>
		<style>
			body {
				background-color: #f8f9fa;
			}
			.login-container {
				max-width: 400px;
				margin: 100px auto;
				padding: 20px;
				background-color: white;
				border-radius: 5px;
				box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
			}
			.login-header {
				text-align: center;
				margin-bottom: 30px;
			}
			.login-header h1 {
				font-size: 24px;
				color: #333;
			}
			.form-group {
				margin-bottom: 20px;
			}
			.btn-login {
				width: 100%;
				padding: 10px;
				font-size: 16px;
			}
			.error-message {
				color: #dc3545;
				margin-bottom: 15px;
				text-align: center;
			}
		</style>
	</head>
	<body>
		<div class="container">
			<div class="login-container">
				<div class="login-header">
					<h1>Teacher Login</h1>
					<p class="text-muted">Student Submission System</p>
				</div>

				<% if (request.getParameter("error") != null) { %>
				<div class="error-message">
					Invalid email or password. Please try again.
				</div>
				<% } %>

				<form action="j_security_check" method="POST">
					<div class="form-group">
						<label for="j_username">Email:</label>
						<input
							type="email"
							class="form-control"
							id="j_username"
							name="j_username"
							required
							autofocus
						/>
					</div>

					<div class="form-group">
						<label for="j_password">Password:</label>
						<input
							type="password"
							class="form-control"
							id="j_password"
							name="j_password"
							required
						/>
					</div>

					<button type="submit" class="btn btn-primary btn-login">Login</button>
				</form>
			</div>
		</div>

		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
	</body>
</html>
