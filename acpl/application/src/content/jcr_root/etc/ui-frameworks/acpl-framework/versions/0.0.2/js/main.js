(function () {
  // Fix brand link — navbar-brand renders with href="#" by default
  var brand = document.querySelector('.navbar-brand');
  if (brand && (!brand.href || brand.getAttribute('href') === '#')) {
    var path = window.location.pathname;
    var siteRoot = path.match(/^(\/content\/sites\/[^/]+)/);
    if (siteRoot) {
      brand.setAttribute('href', siteRoot[1] + '.html');
    }
  }

  // Scroll-shrink navbar
  var nav = document.querySelector('.navbar');
  if (nav) {
    var scrolled = false;
    window.addEventListener('scroll', function () {
      var y = window.scrollY || window.pageYOffset;
      if (y > 40 && !scrolled) {
        nav.classList.add('nav-scrolled');
        scrolled = true;
      } else if (y <= 40 && scrolled) {
        nav.classList.remove('nav-scrolled');
        scrolled = false;
      }
    }, { passive: true });
  }
})();
