$(document).ready(function() {
	$("#searchToggle").click(function(e) {
		e.stopPropagation(); // 이벤트 버블링 방지
		$("#searchBox").toggle(); // searchBox 표시 상태 토글
	});

	$(document).on("click", function(e) {
		if (!$(e.target).closest("#searchBox").length) {
			// 클릭한 영역이 #searchBox 내부가 아니면
			$("#searchBox").hide(); // searchBox 숨김
		}
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

	var timer; // 전역 변수로 타이머를 선언합니다.

	$(".search-input").on("input", function() {
		clearTimeout(timer); // 이전의 타이머를 제거합니다.
		var searchKeyword = $(this).val();
		var $input = $(this); // 현재 입력 필드를 저장합니다.

		if (searchKeyword.length === 0) {
			$("#searchResult").html("검색 결과가 여기에 나타납니다.");
			return; // 검색어가 없으면 더 이상 진행하지 않습니다.
		}

		// 새로운 타이머를 설정합니다.
		timer = setTimeout(function() {
			$.ajax({
				url: "/SearchResult",
				type: "GET",
				data: {
					keyword: searchKeyword
				},
				success: function(response) {
					$("#searchResult").html(response);
					$("#searchBox").show(); // 검색 결과가 있을 때 searchBox를 보입니다.
					$input.focus(); // AJAX 호출 성공 후 입력 필드에 포커스를 다시 설정합니다.
				},
				error: function(error) {
					console.error("Error: " + error);
					$("#searchResult").html("검색 결과를 불러오는데 실패했습니다.");
					$input.focus(); // AJAX 호출 실패 후에도 입력 필드에 포커스를 다시 설정합니다.
				}
			});
		}, 300); // 300밀리초(0.3초) 후에 검색 요청을 보냅니다.
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
			window.location.href = "/TagResult/" + target;
		}
	});
});