<h1>Videos Watched: ${model.videosWatched}</h1>
<ul>
    <#list model.videos as video>
        <li>
            <form action="/record-viewing/${video.id}" method="post">
                <button type="submit">Record View for ${video.name}</button>
            </form>
        </li>
    </#list>
</ul>
<form action="/upload-video" method="post">
    <label for="name">Video Name</label>
    <input type="text" id="name" name="name"/>
    <button type="submit">Upload Video</button>
</form>

<ul>
    <#list model.users as user>
        <li>${user.email} : ${user.id}</li>
    </#list>
</ul>