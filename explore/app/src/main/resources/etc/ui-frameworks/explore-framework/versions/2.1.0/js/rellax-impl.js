/*
 *      Copyright (C) 2020  Kestros, Inc.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

document.addEventListener('DOMContentLoaded', () => {
  new Rellax('.rellax-slow', {
    speed: -2,
    center: false,
    wrapper: null,
    round: true,
    vertical: true,
    horizontal: false,
    zindex: -1
  });

  new Rellax('.rellax-slower', {
    speed: -7,
    center: false,
    wrapper: null,
    round: true,
    vertical: true,
    horizontal: false,
    zindex: -1
  });

  new Rellax('.rellax-slowest', {
    speed: -10,
    center: false,
    wrapper: null,
    round: true,
    vertical: true,
    horizontal: false,
    zindex: -1
  });

  new Rellax('.rellax-fast', {
    speed: 5,
    center: false,
    wrapper: null,
    round: true,
    vertical: true,
    horizontal: false,
    zindex: -1
  });
  new Rellax('.rellax-faster', {
    speed: 7,
    center: false,
    wrapper: null,
    round: true,
    vertical: true,
    horizontal: false,
    zindex: -1
  });
  new Rellax('.rellax-fastest', {
    speed: 10,
    center: false,
    wrapper: null,
    round: true,
    vertical: true,
    horizontal: false,
    zindex: -1
  });
})

