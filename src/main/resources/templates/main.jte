
@template.layout(
    content = @`
        <div class="bg-success text-white p-5 text-center">
            <p class="fs-1">Page analyzer</p>
            <p class="fs-4">Check SEO information for free</p>
            <form>
                <input class="form-control-lg w-75" type="text" name="siteAddress" placeholder="Enter site address as in example below 👇">
                <input class="form-control-lg bg-black text-white" type="submit" value="Check">
            </form>
            <p class="fs-5">Example: https://mail.ru</p>
        </div>
    `
)