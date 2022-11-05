$(document).ready(function() {
	var searchFieldButton = $("#searchFieldButton");
	
	$(searchFieldButton).on('click', function(e){ 
	var searchField = $("#searchField").val();
	console.log("searchField : " + searchField);
	
   		$.ajax({
        	 type: "post",
         	url : "/field/search",
         	data : {searchField : searchField},
         	success : function(res){ 
				//console.log(res.substring(res.indexOf('<div class="row p-5" id="findFields">'), res.indexOf('<span id="findFieldsEnd">')));
         	   $("#findFields").replaceWith(res.substring(res.indexOf('<div class="row p-5" id="findFields">'), res.indexOf('<span id="findFieldsEnd">')));
         	}
    	});	
})	
});
	
	





