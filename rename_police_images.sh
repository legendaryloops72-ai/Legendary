#!/bin/bash
DIR="app/src/main/res/drawable"
for f in $DIR/img_police_*.jpg; do
    # Get base name without extension and without timestamp
    # Example: img_police_patrol_12345.jpg -> img_police_patrol
    base=$(basename "$f" .jpg)
    new_base=$(echo "$base" | sed -E 's/_[0-9]+$//')
    mv "$f" "$DIR/$new_base.jpg"
done
