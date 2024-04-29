  $(document).ready(function() {

        const user_idx = $('#user_idx').val(); // 현재 사용자 ID
        const user_id = $('#user_id').val(); // 현재 사용자 ID
        const to_id = $('#user_id').val(); // 현재 사용자 ID

        function updateDetailLike() {
            $('.dlike_btn').each(function() {
                const button = $(this);
                const post_idx = button.data('postdetail-idx');
                const svg = button.find('svg'); // SVG 요소 선택

                $.ajax({
                    url: `/CheckLike?post_idx=` + post_idx + '&user_idx=' + user_idx,
                    type: 'GET',
                    dataType: 'json',
                    success: function(isLiked) {
                        button.data('liked', isLiked);
                        // isLiked 상태에 따라 SVG fill 속성 변경
                        if (isLiked) {
                            svg.attr('fill', 'red');
                        } else {
                            svg.attr('fill', 'none');
                        }
                    },
                    error: function(error) {
                        console.error('Error:', error);
                    }
                });
            });
        }

        updateDetailLike();

        $('.dlike_btn').click(function() {
            const button = $(this);
            const post_idx = button.data('postdetail-idx');
            const post_id = button.data('postdetail-id');
            const noti_idx = button.data('noti-idx');
            const isLiked = button.data('liked');
            const message = button.data('message-number');

            if (isLiked) {
                $.ajax({
                    url: '/LikeDelete',
                    type: 'DELETE',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        post_idx: post_idx,
                        user_idx: user_idx
                    }),
                    success: function(response) {
                        updateDetailLike();
                        $.ajax({
                            url: '/LoadLikes',
                            type: 'POST',
                            data: {
                                "post_idx": post_idx,
                                "user_idx": user_idx
                            },
                            success: function(loadlikes) {
                                $('#postdetail_likes' + noti_idx).val(loadlikes);
                            },
                            error: function() {
                                alert('오류가 발생했습니다. 다시 시도해주세요.');
                            }
                        });
                    },
                    error: function(error) {
                        console.error('Error:', error);
                        alert('오류가 발생했습니다. 다시 시도해주세요.');
                    }
                });
            } else {
                $.ajax({
                    url: '/LikeAdd',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        post_idx: post_idx,
                        user_idx: user_idx
                    }),
                    success: function(response) {
                        updateDetailLike();
                        $.ajax({
                            url: '/LoadLikes',
                            type: 'POST',
                            data: {
                                "post_idx": post_idx,
                                "user_idx": user_idx
                            },
                            success: function(loadlikes) {
                                $('#postdetail_likes' + noti_idx).val(loadlikes);
                            },
                            error: function() {
                                alert('오류가 발생했습니다. 다시 시도해주세요.');
                            }
                        });
                        $.ajax({
                            url: '/AddNoti',
                            type: 'POST',
                            data: {
                                "post_id": post_id,
                                "user_id": user_id,
                                "post_idx": post_idx,
                                "message": message
                            },
                            success: function(response) {
                                console.log('Add 성공');
                            },
                            error: function() {
                                alert('오류가 발생했습니다. 다시 시도해주세요.');
                            }
                        });

                    },
                    error: function(error) {
                        console.error('Error:', error);
                        alert('오류가 발생했습니다. 다시 시도해주세요.');
                    }
                });
            }
        });

    });