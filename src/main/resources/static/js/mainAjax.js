$(document).ready(function() {
	const csrfToken = $('meta[name="_csrf"]').attr('content');
	const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
	$(document).on('click', '.notis_link', function(e) {
		e.preventDefault();
		var postIdx = $(this).data('post-idx');
		var userIdx = $(this).data('user-idx');
		var notiIdx = $(this).data('noti-idx');

		$.ajax({
			url: '/getPostDetail',
			type: 'GET',
			data: {
				post_idx: postIdx,
				user_idx: userIdx,
				noti_idx: notiIdx
			},
			beforeSend: function(xhr) {
				xhr.setRequestHeader(csrfHeader, csrfToken);
			},
			success: function(response) {
				$('#postDetailModalContainer').html(response);
				$('#postnotidetail' + postIdx).modal('show');
			},
			error: function() {
				alert('오류가 발생했습니다. 다시 시도해주세요.');
			}
		});
	});

$(document).on('hidden.bs.modal', '.noti_modal', function(e) {
    var notiIdx = $(this).attr('data-noti-idx');
    console.log(notiIdx);
    var $modal = $(this); // 모달을 변수에 저장

    $.ajax({
        url: '/CheckNoti',
        type: 'POST',
        data: { notification_idx: notiIdx },
        beforeSend: function(xhr) {
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(response) {
            // 저장한 모달 변수를 사용하여 모달과 배경을 모두 제거
            $modal.remove();
            $('.modal-backdrop').remove();
        }
    });
});



});

