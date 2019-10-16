<!DOCTYPE html>
<html>

<head>
    <meta charset='utf-8'>
    <meta http-equiv='X-UA-Compatible' content='IE=edge'>
    <title>Screen Capture</title>
    <meta name='viewport' content='width=device-width, initial-scale=1'>
    <link rel='stylesheet' type='text/css' media='screen' href='../main.css'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
    <link href="https://fonts.googleapis.com/css?family=Open+Sans&display=swap" rel="stylesheet">
	<link rel="icon" type="image/png" href="../static/favicon.ico">
</head>

<body>
    <div class="main">
        <div class="center-align">
            <h4 class="card-panel pink lighten-3">Simple screenshot software written in Java</h4>
            <a class="waves-effect waves-light btn release z-depth-2"
                href="https://github.com/toppev/screen-capture/releases/latest/download/screen-capture.jar"><i
                    class="material-icons"></i>Download Latest</a>
            <a class="waves-effect waves-light btn release z-depth-2"
                href="https://github.com/toppev/screen-capture/releases/"><i class="material-icons"></i>All Releases</a>
        </div>
        <div class="center-align">
            <h4 class=""></h4>
            <img class="responsive-img z-depth-5" src="../static/screenshot.png" alt="screenshot" id="screenshot">
            <h6 class="desc">Draw and type with different colors on your screenshot or blank image. Import, upload or save using the shortcuts.</h6>
        </div>
        <div class="container" id="shortcuts">
            <div class="row">
                <div class="col s8 offset-s2">
                    <div class="card teal lighten-1">
                        <table class="striped responsive-table highlight centered">
                            <thead>
                                <tr>
                                    <th>Shortcut</th>
                                    <th>Description</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td>ESC</td>
                                    <td>Quit</td>
                                </tr>
                                <tr>
                                    <td>CTRL + N</td>
                                    <td>Open the screenshot area selection frame</td>
                                </tr>
                                <tr>
                                    <td>CTRL + A</td>
                                    <td>Take a fullscreen screenshot</td>
                                </tr>
                                <tr>
                                    <td>CTRL + U</td>
                                    <td>Upload the image to Imgur</td>
                                </tr>
                                <tr>
                                    <td>CTRL + S</td>
                                    <td>Save the image to disk</td>
                                </tr>
                                <tr>
                                    <td>CTRL + I</td>
                                    <td>Import image from disk</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <footer class="page-footer pink lighten-2">
            <div class="container center-align">
                <div class="row">
                    <div class="col offset-s4 s2">
                        <a href="https://github.com/toppev/screen-capture/" class="github">
                            <p>App Source</p>
                            <img alt="GitHub" src="../static/GitHub-Mark-32px.png" class="">
                        </a>
                    </div>
                    <div class="col s2">
                        <a href="https://github.com/toppev/img-host/" class="github">
                            <p>Web Source</p>
                            <img alt="GitHub" src="../static/GitHub-Mark-32px.png" class="">
                        </a>
                    </div>
                </div>
                <div class="footer-copyright">
                    <div class="container">
                        © 2019 Copyright Text
                    </div>
                </div>
            </div>
        </footer>
    </div>
</body>

</html>