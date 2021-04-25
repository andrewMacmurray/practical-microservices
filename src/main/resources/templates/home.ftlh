<div>
    <#if account.loggedIn>
        <a href="/logout">Logout</a>
    <#else>
        <a href="/login">Login</a>
    </#if>
    <a href="/register">Register</a>

</div>

<h2>Logged in as: ${(account.email)!'anonymous'}</h2>
<h1>Total Views for all Videos: ${model.totalViews}</h1>
<ul>
    <#list model.videos as video>
        <li>
            <#if account.loggedIn>
                <h3>Video by: ${video.user.email}</h3>
                <form action="/record-viewing/${video.id}" method="post">
                    <button type="submit">Record View for ${(video.name)!video.uri}</button> ${video.views}
                </form>
                <form action="/name-video/${video.id}" method="post">
                    <label for="name">Name</label>
                    <input type="text" name="name" id="name">
                    <button type="submit">Name Video</button>
                </form>
            <#else>
                <h3>Video by: ${video.user.email}: ${(video.name)!video.uri} ${video.views}</h3>
            </#if>
        </li>
    </#list>
</ul>
<#if account.loggedIn>
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
</#if>
