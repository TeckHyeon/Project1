$(document).ready(function() {
	$('.search-input').keydown(function(e) {
		const button = $(this);
		const keyword = button.val();
       $.ajax({
		            url: '/SearchResult',
		            type: 'GET',
		            data: {"keyword": keyword},
		            success: function(lists) {
		                let listsHtml = '';
		                lists.forEach(list => {
		                    listsHtml += `
		                        <div class="mt-3">
		                            <div class="border px-3 py-2">
		                                <div class="d-flex justify-content-between">
		                                    <p>${list.from_name}</p>
		                                </div>
		                            </div>
		                        </div>`;
		                });
		                // 특정 게시물의 댓글 섹션에 댓글 목록을 업데이트합니다.
		                // 예를 들어, 게시물 별로 고유한 댓글 섹션 ID를 가진다고 가정할 때
		                $(`#searchResult`).html(listsHtml);
		            },
		            error: function() {
		                alert('댓글을 불러오는 데 실패했습니다. 다시 시도해주세요.');
		            }
		        });
	});
});
