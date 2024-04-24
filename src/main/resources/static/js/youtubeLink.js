$(document).ready(function() {
    $('.content').each(function() {
        let htmlContent = $(this).html();
        const youtubeLinkPattern = /((?:.*?)(?:<br>|$))/g; // 모든 텍스트를 줄단위로 매칭합니다.

        htmlContent = htmlContent.split(youtubeLinkPattern).map(part => {
            if (/(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]+)(?=[^\S]|$)/.test(part)) {
                // 유튜브 링크를 포함하는 줄에 대해 처리합니다.
                return part.replace(/(?:https?:\/\/)?(?:www\.)?(?:youtube\.com\/watch\?v=|youtu\.be\/)([a-zA-Z0-9_-]+)(?=[^\S]|$)/g, function(match, videoId) {
                    // 유튜브 링크를 iframe으로 대체합니다.
                    const iframe = `<iframe width="560" height="315" src="https://www.youtube.com/embed/${videoId}" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>`;
                    return `<div class="row">${iframe}</div>`; // iframe을 row 클래스를 가진 div로 감쌉니다.
                });
            } else {
                // 유튜브 링크가 없는 줄에 대해 처리합니다.
                return `<div class="row">${part}</div>`; // 단순히 row 클래스를 가진 div로 감쌉니다.
            }
        }).join('');

        // 수정된 내용으로 content의 HTML을 업데이트합니다.
        $(this).html(htmlContent);
    });
});