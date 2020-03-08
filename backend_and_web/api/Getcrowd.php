<?php
// TODO: limit by radius??
header('Access-Control-Allow-Origin: *');

include_once('db_config.php');

$posts_array = array();
$query = "select * from crowdalerts";
$result = mysql_query($query);
while ($posts_row = mysql_fetch_assoc($result))
{
	    array_push($posts_array, $posts_row);
}
mysql_close($conn);
/* JSON Response */
echo json_encode($posts_array);

?>
