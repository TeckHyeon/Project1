$(document).ready(function() {
    $(".tag-btn").click(function() {
        var tagName = $(this).data("tag-name"); // 버튼에서 tag_name을 가져옵니다.
        $.ajax({
            url: "/TagResult/" + tagName,
            type: "GET",
            success: function(response) {
				console.log(tagName);
                $("#section").html(response); // 성공 시, 응답으로 섹션의 HTML을 업데이트합니다.
            },
            error: function(error) {
                console.error("Error: " + error); // 오류 발생 시 콘솔에 로그를 출력합니다.
            }
        });
    });
});
