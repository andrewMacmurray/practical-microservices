<h2>Logged in as: ${(account.email)!'anonymous'}</h2>
<h1>Videos Watched: ${model.videosWatched}</h1>
<ul>
    <#list model.videos as video>
        <li>
            <form action="/record-viewing/${video.id}" method="post">
                <button type="submit">Record View for ${video.uri}</button> ${video.views}
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