@import hexlet.code.utils.NamedRoutes
@import gg.jte.Content

@param Content content


<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
              crossorigin="anonymous">
        <title>Page analyzer 📊</title>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <header class="bg-black text-white p-2 pt-3">
            <p>
                <span class="px-3 fs-2">Page analyzer</span>
                <a href="${NamedRoutes.rootPath()}" class="pe-3 text-white">Main page</a>
                <a href="${NamedRoutes.urlsPath()}" class="text-white">Sites list</a>
            </p>
        </header>
        <main class="flex-grow-1">
            ${content}
        </main>
        <footer>
            <p class="footer border-top py-3 mt-5 bg-light text-center mb-0">
                This site was created 📝 by
                <a href="https://github.com/roman-iork" class="text-black">Roman Iork</a>
                during studying 📖 at
                <a href="https://ru.hexlet.io" class="text-black">Hexlet</a>
            </p>
        </footer>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
                crossorigin="anonymous"></script>
    </body>
</html>