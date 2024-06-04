function refreshProfilePage(userName) {
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
	console.log("user_name = " + userName);
	$.ajax({
		url: '/profile/' + userName,
		type: 'GET',
		headers: {
			'X-Requested-With': 'XMLHttpRequest'
		},
		beforeSend: function(xhr) {
			xhr.setRequestHeader(csrfHeader, csrfToken);
		},
		success: function(response) {
			$('#section').html(response);
			attachFollowButtonHandlers(); // 이벤트 핸들러 다시 적용
		},
		error: function(error) {
			console.error('Error refreshing profile page:', error);
		}
	});
}

function attachFollowButtonHandlers() {
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
	const login_user = $('#user_id').val();

	const follow_btn = $('.follow_btn');

	follow_btn.click(function() {
		const button = $(this);
		const user_id = button.data('user-id');
		const following = button.data('following');
		var isLoggedIn = $('#isLoggedIn').val() === 'true';
		if (isLoggedIn) {
			if (following) {
				$.ajax({
					url: `/DeleteFollow/${user_id}/${login_user}`,
					type: 'DELETE',
					beforeSend: function(xhr) {
						xhr.setRequestHeader(csrfHeader, csrfToken);
					},
					success: function(response) {
						alert('팔로우가 해제되었습니다.');
						CheckFollow(); // 모든 팔로우 버튼 상태 갱신
					},
					error: function(error) {
						console.error('Error:', error);
						alert('오류가 발생했습니다. 다시 시도해주세요.');
					}
				});
			} else {
				$.ajax({
					url: `/InsertFollow/${user_id}/${login_user}`,
					type: 'POST',
					beforeSend: function(xhr) {
						xhr.setRequestHeader(csrfHeader, csrfToken);
					},
					success: function(response) {
						alert('팔로우가 추가되었습니다.');
						CheckFollow(); // 모든 팔로우 버튼 상태 갱신
					},
					error: function(error) {
						console.error('Error:', error);
						alert('오류가 발생했습니다. 다시 시도해주세요.');
					}
				});
			}
		} else {
			window.location.href = '/login';
		}
	});

	CheckFollow(); // 새로운 버튼에 대해 팔로우 상태를 다시 확인
}

function CheckFollow() {
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
	const login_user = $('#user_id').val();

	var isLoggedIn = $('#isLoggedIn').val() === 'true';
	if (isLoggedIn) {
		$('.follow_btn').each(function() {
			const button = $(this);
			const user_id = button.data('user-id');
			$.ajax({
				url: `/CheckFollow/` + user_id + `/` + login_user,
				type: 'GET',
				dataType: 'json',
				beforeSend: function(xhr) {
					xhr.setRequestHeader(csrfHeader, csrfToken);
				},
				success: function(following) {
					button.data('following', following);
					button.toggleClass('btn-primary', following)
						.toggleClass('btn-outline-secondary', !following)
						.val(following ? '팔로잉' : '팔로우');
				},
				error: function(error) {
					console.error('Error:', error);
					alert('오류가 발생했습니다. 다시 시도해주세요.');
				}
			});
		});
	}
}

// document.ready start
$(document).ready(function() {
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
	const login_user = $('#user_id').val();
	const userName = $("#userName").attr('value');

	// Profile 페이지 비동기 통신으로 다시 불러오기
	refreshProfilePage(userName);

	$(".profile-img").click(function() {
		$("#profileInput").click();
	});

	$("#profileInput").change(function() {
		if (confirm("프로필 사진을 변경하겠습니까?")) {
			var formData = new FormData();
			formData.append('file', $('#profileInput')[0].files[0]);

			$.ajax({
				url: '/updateProfile',
				type: 'POST',
				data: formData,
				beforeSend: function(xhr) {
					xhr.setRequestHeader(csrfHeader, csrfToken);
				},
				processData: false,
				contentType: false,
				success: function(data) {
					alert("프로필 사진이 변경되었습니다.");
					location.reload();
				},
				error: function(e) {
					alert("오류가 발생했습니다.");
				}
			});
		}
	});


	$(document).on('hidden.bs.modal', '#followerModal', function(e) {
		const userName = $("#userName").attr('value');
		refreshProfilePage(userName);
		console.log("refreshProfilePage 작동");
	});

	$(document).on('hidden.bs.modal', '#followModal', function(e) {
		const userName = $("#userName").attr('value');
		refreshProfilePage(userName);
		console.log("refreshProfilePage 작동");
	});


	attachFollowButtonHandlers(); // 초기 로드 시 이벤트 핸들러 적용
});
