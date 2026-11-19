/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */
fetch('http://localhost:8080/tranajo/ServletProyecto/api/users',{
    method:'POST',
    headers:{
        'Content-Type':'application/html'
    },
    body:'nombre=Carlos'
})
        .then(responsive => response.text())
        .then(data => console.log(data))
        .then(error => console.error('Error:',error));
   