@import hexlet.code.dto.UrlPage
@import hexlet.code.utils.NamedRoutes

@param UrlPage page

@template.layout(
    content = @`
        @if(page.getFlash() != null)
            <p class="alert ${page.getFlashStatus()}" role="alert">
                ${page.getFlash()}
                <a href="${NamedRoutes.urlPath(page.getUrl().getId())}" class="position-absolute top-50 start-100 translate-middle pe-5">❌</a>
            </p>
        @endif
        <div class="container">
            <h2 class="p-3">Site: <span class="fs-3 text-success"><i>${page.getUrl().getName()}</i></span></h2>
            <table class="table table-striped">
                <tbody>
                    <tr>
                        <th scope="row">id</th>
                        <td>${page.getUrl().getId()}</td>
                    </tr>
                    <tr>
                        <th scope="row">Name</th>
                        <td>${page.getUrl().getName()}</td>
                    </tr>
                    <tr>
                        <th scope="row">Created</th>
                        <td>
                            ${page.getUrl().getCreatedAt().toLocalDateTime().toLocalDate().toString()}
                            ${page.getUrl().getCreatedAt().toLocalDateTime().toLocalTime().withNano(0).toString()}
                        </td>
                    </tr>
                </tbody>
            </table>
            <h2 class="px-3 pt-5 pb-2">Check chronology</h2>
            <form class="px-3 pb-3" action="${NamedRoutes.checkPath(page.getUrl().getId())}" method="post">
                <input class="px-3 py-1 bg-warning text-dark bg-opacity-50" type="submit" value="One more check  ➕">
            </form>
            <table class="table table-bordered">
                <thead>
                    <tr class="table-success">
                        <th scope="col">id</th>
                        <th scope="col">Response</th>
                        <th scope="col">Title</th>
                        <th scope="col">h1</th>
                        <th scope="col">Description</th>
                        <th scope="col">Check date</th>
                    </tr>
                </thead>
                <tbody>
                @for(var urlCheck : page.getUrlChecks())
                    <tr>
                        <th scope="row">${urlCheck.getId()}</th>
                        <td>${urlCheck.getStatusCode()}</td>
                        <td>${urlCheck.getTitle()}</td>
                        <td>${urlCheck.getH1()}</td>
                        <td>${urlCheck.getDescription()}</td>
                        <td>
                            ${urlCheck.getCreatedAt().toLocalDateTime().toLocalDate().toString()}
                            ${urlCheck.getCreatedAt().toLocalDateTime().toLocalTime().withNano(0).toString()}
                        </td>
                    </tr>
                @endfor
                </tbody>
            </table>
        </div>
    `
)