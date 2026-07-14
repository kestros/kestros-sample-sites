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

// Detail pages (club/player/match/story) highlight their parent section tab.
(function () {
  document.addEventListener('DOMContentLoaded', function () {
    if (document.querySelector('.navbar-start .navbar-item.is-active')) {
      return;
    }
    var path = window.location.pathname;
    var section = null;
    if (path.indexOf('/teams/') !== -1) { section = '/teams.html'; }
    else if (path.indexOf('/players/') !== -1) { section = '/players.html'; }
    else if (path.indexOf('/matches/') !== -1) { section = '/matches.html'; }
    else if (path.indexOf('/stories/') !== -1) { section = '/stories.html'; }
    if (!section) { return; }
    document.querySelectorAll('.navbar-start .navbar-item').forEach(function (item) {
      if (item.getAttribute('href') && item.getAttribute('href').indexOf(section) !== -1) {
        item.classList.add('is-active');
      }
    });
  });
})();
