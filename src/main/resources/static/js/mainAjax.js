$(document).ready(function() {
    $("body").on("click", ".tag-btn", function() {
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
    // profile_btn을 가진 요소가 아니라, document 내의 모든 요소에 대한 클릭 이벤트를 감지하고, 그 중에서 profile_btn 클래스를 가진 요소를 필터링하여 처리합니다.
    $(document).on("click", ".profile_btn", function() {
        var id = $(this).data("profile-id");
        $.ajax({
            url: "/profileSection/" + id,
            type: "GET",
            success: function(response) {
                console.log(id);
                $("#section").html(response);
            },
            error: function(error) {
                console.error("Error: " + error);
            }
        });
    });
    
});
