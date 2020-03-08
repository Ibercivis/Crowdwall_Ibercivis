<?php 

	include_once('db_config.php');
	function isOkGet(){
		if($_GET['lat']!=0 && $_GET['lon']!=0)
			return true;
		else
			return true;
	}
	if(isOkGet()){
		$lat = isset($_GET['lat']) ? mysql_real_escape_string($_GET['lat']) : "";
		$lon = isset($_GET['lon']) ? mysql_real_escape_string($_GET['lon']) : "";
		$type = isset($_GET['type']) ? mysql_real_escape_string($_GET['type']) : "";
		$query= "INSERT INTO crowdalerts (id, type, lat, lon)  values (0,'".$type."',$lat,$lon)";
		$insert = mysql_query($query);
		print_r($insert);
		if($insert)
			return True;
		else
			return False;
		
	}

?>
