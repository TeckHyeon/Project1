document.querySelectorAll('.toggleButton').forEach(button => {
	button.addEventListener('click', function() {
		const content = document.getElementById(this.getAttribute('data-target'));
		const expanded = this.getAttribute('aria-expanded') === 'true';
		if (expanded) {
			content.style.maxHeight = '0px';
			this.setAttribute('aria-expanded', 'false');
		} else {
			content.style.maxHeight = content.scrollHeight + 'px';
			this.setAttribute('aria-expanded', 'true');
		}
	});
});