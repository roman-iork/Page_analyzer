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
    <body>
        <header class="bg-black text-white p-2 pt-3">
            <p>
                <span class="px-3 fs-2">Page analyzer</span>
                <a href="/" class="pe-3 text-white">Main page</a>
                <a href="/list" class="text-white">Sites list</a>
            </p>
        </header>
        <main>
            ${content}
        </main>
        <footer>
            <div class="border-top position-fixed top-100 start-50 translate-middle pb-5 w-100 bg-light text-dark">
                <p class="text-center pt-3">
                    This site was created 📝 by
                    <a href="https://github.com/roman-iork" class="text-black">Roman Iork</a>
                    during studying 📖 at
                    <a href="https://ru.hexlet.io" class="text-black">Hexlet</a>
                </p>
            </div>
        </footer>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
                crossorigin="anonymous"></script>
    </body>
</html>