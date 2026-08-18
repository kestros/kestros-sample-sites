// Client-side match filter (results / schedule). No page reload: the datasource renders every match,
// the match-filter component renders the dropdowns, and this hides non-matching rows/matchweek groups.
(function () {
  var mwSelect = document.getElementById('acpl-filter-mw');
  var clubSelect = document.getElementById('acpl-filter-club');
  if (!mwSelect || !clubSelect) {
    return;
  }
  var groups = document.querySelectorAll('.acpl-mw-group');

  function apply() {
    var mw = mwSelect.value;
    var club = clubSelect.value;
    groups.forEach(function (group) {
      var heading = group.querySelector('h5');
      var groupMw = heading ? (heading.textContent.match(/\d+/) || [''])[0] : '';
      var mwMatches = !mw || groupMw === mw;
      var rows = group.querySelectorAll('.result-row, .fixture-row');
      var visible = 0;
      rows.forEach(function (row) {
        var clubMatches = !club
          || row.getAttribute('data-acpl-home') === club
          || row.getAttribute('data-acpl-away') === club;
        var show = mwMatches && clubMatches;
        row.style.display = show ? '' : 'none';
        if (show) {
          visible += 1;
        }
      });
      group.style.display = (mwMatches && visible > 0) ? '' : 'none';
    });
  }

  // Deep links: ?club=<slug> / ?mw=<n> preselect the filters (used by club-page shortcuts)
  var params = new URLSearchParams(window.location.search);
  var qClub = params.get('club');
  var qMw = params.get('mw');
  if (qClub && clubSelect.querySelector('option[value="' + qClub + '"]')) {
    clubSelect.value = qClub;
  }
  if (qMw && mwSelect.querySelector('option[value="' + qMw + '"]')) {
    mwSelect.value = qMw;
  }
  if (qClub || qMw) {
    apply();
  }

  mwSelect.addEventListener('change', apply);
  clubSelect.addEventListener('change', apply);
  var clear = document.getElementById('acpl-filter-clear');
  if (clear) {
    clear.addEventListener('click', function () {
      mwSelect.value = '';
      clubSelect.value = '';
      apply();
    });
  }
})();
