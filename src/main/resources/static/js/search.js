$(document).ready(function() {
	$("#searchToggle").click(function(e) {
		e.stopPropagation(); // 이벤트 버블링 방지
		$("#searchBox").toggle(); // searchBox 표시 상태 토글
	});

	$(document).click(function() {
		$("#searchBox").hide(); // 외부 클릭 시 searchBox 숨김
	});

	$("#searchBox").click(function(e) {
		e.stopPropagation(); // searchBox 내부 클릭 시 이벤트 버블링 방지
	});

	// 'X' 아이콘 클릭 이벤트
	$(".clear-icon").click(function() {
		$(this).siblings("input").val("").focus();
		$(".clear-icon").hide(); // 'X' 아이콘 숨김
		$(".search-icon").show(); // 돋보기 아이콘 다시 표시
	});

	// 입력란에 값 입력 시 'X' 아이콘 표시
	$(".search-box input").on("input", function() {
		if ($(this).val() == "") {
			$(".clear-icon").hide();
			$(".search-icon").show(); // 입력란이 비었을 때 돋보기 아이콘 표시
		} else {
			$(".clear-icon").show();
			$(".search-icon").hide(); // 입력란에 텍스트가 있을 때 돋보기 아이콘 숨김
		}
	});

	$(".search-input").on("input", function() {
		var searchKeyword = $(this).val();

		if (searchKeyword.length === 0) {
			$("#searchResult").html("검색 결과가 여기에 나타납니다.");
			return;
		}

		$.ajax({
			url: "/SearchResult",
			type: "GET",
			data: {
				keyword: searchKeyword
			},
			dataType: "json",
			success: function(response) {
				var resultHtml = "";
				$.each(response, function(index, item) {
					if (item.type === "user") {
						resultHtml += "<div class='search-item d-flex' data-type='user' data-target='" + item.id + "'>";
						resultHtml += "<image alt='프로필' src='" + item.image + "'";
						resultHtml += "style='width: 50px; height: 50px;' class='rounded-circle me-3'>";
						resultHtml += "<div>"
						resultHtml += "<div class='d-flex justify-content-between'>"
						resultHtml += "<h5>" + item.name + "</h5><span> 팔로워 : " + item.count + "</span></div>";
						resultHtml += "<p style='font-size: 0.85rem;'>" + item.description + "</p>";
						resultHtml += "</div></div>"
					} else if (item.type === "tag") {
						resultHtml += "<div class='search-item' data-type='tag' data-target='" + item.id + "'><h5>" + item.name + "</h5><span>Count: " + item.count + "</span></div>";
					}
				});

				$("#searchResult").html(resultHtml);
			},
			error: function(xhr, status, error) {
				console.error("Error: " + error);
				$("#searchResult").html("검색 결과를 불러오는데 실패했습니다.");
			}
		});
	});
$("#searchResult").on('click', '.search-item', function() {
    console.log("click");
    var type = $(this).data("type");
    var target = $(this).data("target");
		
    if (type === "user") {
        // 유저 프로필 페이지로 이동
        window.location.href = "/profile/" + target;
    } else if (type === "tag") {
        // 태그 게시물 목록 페이지로 이동
        window.location.href = "/tagPosts/" + target;
    }
});

});