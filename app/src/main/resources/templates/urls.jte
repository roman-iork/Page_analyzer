@import hexlet.code.dto.UrlsPage
@import hexlet.code.utils.NamedRoutes
@import hexlet.code.repository.UrlCheckRepository

@param UrlsPage page

@template.layout(
    content = @`
        @if(page.getFlash() != null)
            <p class="alert ${page.getFlashStatus()}" role="alert">
                ${page.getFlash()}
                <a href="${NamedRoutes.urlsPath()}" class="position-absolute top-50 start-100 translate-middle pe-5">❌</a>
            </p>
        @endif
        <div class="container pt-4">
            <h1>Sites list:</h1>
            @if(!page.getUrls().isEmpty())
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th scope="col">id</th>
                            <th scope="col">Name</th>
                            <th scope="col">Last check</th>
                            <th scope="col">Response</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var url : page.getUrls())
                            <tr>
                                <th scope="row">${url.getId()}</th>
                                <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
                                @if(UrlCheckRepository.findLast(url.getId()).isPresent())
                                    <td>
                                        ${UrlCheckRepository.findLast(url.getId()).get()
                                            .getCreatedAt().toLocalDateTime().toLocalDate().toString()}
                                        ${UrlCheckRepository.findLast(url.getId()).get()
                                            .getCreatedAt().toLocalDateTime().toLocalTime().withNano(0).toString()}
                                    </td>
                                    <td>${UrlCheckRepository.findLast(url.getId()).get().getStatusCode()}</td>
                                @else
                                    <td></td>
                                    <td></td>
                                @endif
                            </tr>
                        @endfor
                    </tbody>
                </table>
            @else
                <p class="pt-4 fs-4">No sites added yet 🤷</p>
            @endif
        </div>
    `
)