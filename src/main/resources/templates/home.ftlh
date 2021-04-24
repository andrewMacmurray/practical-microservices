<h2>Logged in as: ${(account.email)!'anonymous'}</h2>
<h1>Total Views: ${model.totalViews}</h1>
<ul>
    <#list model.videos as video>
        <li>
            <h3>Video by: ${video.user.email}</h3>
            <form action="/record-viewing/${video.id}" method="post">
                <button type="submit">Record View for ${(video.name)!video.uri}</button> ${video.views}
            </form>
            <form action="/name-video/${video.id}" method="post">
                <label for="name">Name</label>
                <input type="text" name="name" id="name">
                <button type="submit">Name Video</button>
            </form>
        </li>
    </#list>
</ul>
<form action="/upload-video" method="post">
    <label for="uri">Video Uri</label>
    <input type="text" id="uri" name="uri"/>
    <button type="submit">Upload Video</button>
</form>

<ul>
    <#list model.users as user>
        <li>${user.email} : ${user.id}</li>
    </#list>
</ul>