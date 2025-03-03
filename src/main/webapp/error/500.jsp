<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
	<head>
		<title>500 Internal Server Error - Student Submission System</title>
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
				max-width: 600px;
				margin: 100px auto;
				padding: 40px;
				background-color: white;
				border-radius: 5px;
				box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
				text-align: center;
			}
			.error-code {
				font-size: 72px;
				font-weight: bold;
				color: #dc3545;
				margin-bottom: 20px;
			}
			.error-message {
				font-size: 24px;
				color: #333;
				margin-bottom: 30px;
			}
			.error-description {
				color: #6c757d;
				margin-bottom: 30px;
			}
			.btn-home {
				padding: 10px 30px;
				font-size: 16px;
			}
		</style>
	</head>
	<body>
		<div class="container">
			<div class="error-container">
				<div class="error-code">500</div>
				<h1 class="error-message">Internal Server Error</h1>
				<p class="error-description">
					Sorry, something went wrong on our end. We're working to fix it.
					Please try again later.
				</p>
				<a
					href="${pageContext.request.contextPath}/"
					class="btn btn-primary btn-home"
				>
					Go to Homepage
				</a>
			</div>
		</div>

		<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
	</body>
</html>
