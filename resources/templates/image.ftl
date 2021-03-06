<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset='utf-8'>
    <meta http-equiv='X-UA-Compatible' content='IE=edge'>
    <title>Image</title>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel='stylesheet' type='text/css' media='screen' href='../static/main.css'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans&display=swap" rel="stylesheet">
    <link rel="icon" type="image/png" href="../static/favicon.ico">
</head>

<body>
<div class="container main">
    <div class="container">
        <div class="card">
            <div class="card-image">
                <img src="./img/${image._id}.png" alt="image">
            </div>
        </div>
        <div class="card-content teal lighten-1 z-depth-2">
            <div class="row">
                <div class="col s2 offset-s3">👁 ${image.views}</div>
                <div class="col s2">Uploaded: ${image.getCreatedFormatted()}</div>
                <div class="col s2">Last Viewed: ${image.getLastViewedFormatted()}</div>
            </div>
        </div>
    </div>
</div>
</body>

</html>