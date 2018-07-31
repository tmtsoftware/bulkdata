
TMT-Bulkdata
============

Report
------
https://docs.google.com/document/d/1L7bLMSYixV2xf42YjDp9IU9eJmzLQfUhNoQyOyNhpCs/edit

Slides
------
https://docs.google.com/presentation/d/1c6NhHBFh-SdMWpIuarHAbSZ7Ia6eOt9op6SG42TdAaU/edit#slide=id.p

How to run
----------
```
./activator run

./activator "backend/runMain tmt.app.Main wavefront wavefront1 dev seed1"

./activator "backend/runMain tmt.app.Main metadata metadata1 dev"

./activator "backend/runMain tmt.app.Main metric metric1 dev"

./activator "backend/runMain tmt.app.Main rotator rotator1 dev"

./activator "backend/runMain tmt.app.Main science-image-source camera1 dev"
```

Data Setup
----------

- Download [video](http://distribution.bbb3d.renderfarming.net/video/mp4/bbb_sunflower_1080p_60fps_normal.mp4)
- Install [ffmpeg](https://www.ffmpeg.org/)
- `mkdir input`
- `cd input`
- Split the video at 20 fps: `ffmpeg -i ../bbb_sunflower_1080p_60fps_normal.mp4  -r 20 image-%05d.jpg`
- `cd ..`
- `mv input /usr/local/data/tmt/frames`
