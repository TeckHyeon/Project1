document.querySelectorAll('.toggleButton').forEach(button => {
	button.addEventListener('click', function() {
		const content = document.getElementById(this.getAttribute('data-target'));
		const expanded = this.getAttribute('aria-expanded') === 'true';
		const md = document.getElementById('md' + this.getAttribute('data-target').replace('content', ''));
		if (expanded) {
			content.style.maxHeight = '0px';
			this.setAttribute('aria-expanded', 'false');
		} else {
			content.style.maxHeight = content.scrollHeight + 'px';
			this.setAttribute('aria-expanded', 'true');
			// 'md' 요소의 높이를 조정하는 로직
			var newMdHeight = content.scrollHeight + md.offsetHeight + 50; // 'content'의 scrollHeight를 추가하여 새로운 높이 계산
			md.style.height = newMdHeight + 'px';
		}
	});
});
