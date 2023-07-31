<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.demo.Entity.WeightRecord" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Weight Records List</title>
    <!-- Add Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        .print-button {
            margin-bottom: 20px;
        }

        .no-print {
            display: none;
        }
    </style>
    <script>
        function printReceipt() {
            var xhr = new XMLHttpRequest();
            xhr.open('GET', '/print', true);
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    window.print();
                }
            };
            xhr.send();
        }

        function resetData() {
            if (confirm("Are you sure you want to reset the data and save it to a file?")) {
                var xhr = new XMLHttpRequest();
                xhr.open('GET', '/reset-and-save', true);
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        alert('Data has been reset and saved to file.');
                        location.reload(); // Refresh the page to show updated data
                    } else if (xhr.readyState === 4 && xhr.status !== 200) {
                        alert('Error occurred while resetting data and saving to file.');
                    }
                };
                xhr.send();
            }
        }

        function deleteRecord(counter) {
    if (confirm("Are you sure you want to delete this record?")) {
        // Get the record ID from the hidden input field
        var recordId = document.getElementById('recordId' + counter).value;

        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/delete-weight', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    alert('Record deleted successfully.');
                    location.reload(); // Refresh the page to show updated data
                } else {
                    alert('Error occurred while deleting record.');
                }
            }
        };
        xhr.send('id=' + encodeURIComponent(recordId));
    }
}
    </script>
</head>
<body>
    <div class="container">
        <h1 class="mt-3">Weight Records List</h1>
        <button class="btn btn-primary mt-3" onclick="printReceipt()">Print Receipt</button>
        <button class="btn btn-danger mt-3" onclick="resetData()">Reset Data</button>
        <table class="table table-bordered mt-3">
            <thead class="table-dark">
                <tr>
                    <th>No.</th>
                    <th>Weight</th>
                    <th>Date</th>
                    <th>Delete</th> <!-- Add a new column for the delete button -->
                </tr>
            </thead>
            <tbody>
                <c:set var="counter" value="1" />
                <c:forEach var="record" items="${weightRecords}">
                    <tr>
                        <td>${counter}</td>
                        <td>${record.weight} Kg</td>
                        <td>${record.timestamp.toLocalDate()}</td>
                        <!-- Add the hidden input field to store the record ID -->
                        <input type="hidden" id="recordId${counter}" value="${record.id}" />
                        <!-- Add the delete button -->
                        <td><button class="btn btn-danger" onclick="deleteRecord('${counter}')">Delete</button></td>
                    </tr>
                    <c:set var="counter" value="${counter + 1}" />
                </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- Add Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.min.js"></script>
</body>
</html>
