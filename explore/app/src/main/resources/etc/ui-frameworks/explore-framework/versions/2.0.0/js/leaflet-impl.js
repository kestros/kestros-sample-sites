document.addEventListener('DOMContentLoaded', () => {
  let singlePinMaps = document.querySelectorAll('.single-pin-map')

  for (let map of singlePinMaps) {

    let leafletMap = L.map(map.id).setView(
        [parseFloat(map.dataset.viewLat), parseFloat(map.dataset.viewLong)],
        parseInt(map.dataset.viewZoom));

    L.tileLayer(
        'https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token=pk.eyJ1IjoiZGRtb3VsdG9uIiwiYSI6ImNrcDdkbXR4azBkZGsyb3FrN3U1OHpqNGMifQ.vSa_4gmyq_WWh_4BFH41gg',
        {
          attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
          maxZoom: 18,
          id: 'mapbox/streets-v11',
          tileSize: 512,
          zoomOffset: -1,
          accessToken: 'your.mapbox.access.token'
        }).addTo(leafletMap);

    let marker = L.marker([parseFloat(map.dataset.markerLat),
      parseFloat(map.dataset.markerLong)]).addTo(leafletMap);

    marker.bindPopup(map.dataset.markerContent);

  }

})