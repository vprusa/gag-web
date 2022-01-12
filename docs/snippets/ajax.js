$.ajax({
  url:"http://localhost:8080/gagweb/api/gesture",
  type:"POST",
  data:'{"dateCreated":1567615738009,"userAlias":"test1","user":{"id":1,"thirdPartyId":"email:admin2@test.com","role":"USER"}}',
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(){

  }
})

$.ajax({
  url:"http://localhost:8080/gagweb/api/fingerdataline",
  type:"POST",
  data:'{"t":1567615738009,"gid":1,"qA":0,"qX":0,"qY":0,"qZ":0,"aX":1,"aY":1,"aZ":1,"p":"MIDDLE"}',
  contentType:"application/json; charset=utf-8",
  dataType:"json",
  success: function(){

  }
})
