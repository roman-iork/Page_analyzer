@import hexlet.code.dto.MainPage
@import hexlet.code.utils.NamedRoutes
@param MainPage page

@template.layout(
    content = @`
        @if(page.getFlash() != null)
            <p class="alert ${page.getFlashStatus()} mb-0" role="alert">
                ${page.getFlash()}
                <a href="${NamedRoutes.rootPath()}" class="position-absolute top-50 start-100 translate-middle pe-5">❌</a>
            </p>
        @endif
        <div class="bg-success text-white p-5 text-center">
            <p class="fs-1">Page analyzer</p>
            <p class="fs-4">Check SEO information for free</p>
            <form action="${NamedRoutes.urlsPath()}" method="post">
                <input class="form-control-lg w-75" type="text" name="url" placeholder="Enter site address as in example below 👇" value="${page.getCanBeUrl()}">
                <input class="form-control-lg bg-black text-white" type="submit" value="Check">
            </form>
            <p class="fs-5">Example: https://domain.smth</p>
        </div>
    `
)