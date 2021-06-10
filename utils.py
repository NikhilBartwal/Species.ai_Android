from PIL import Image
import numpy as np
import streamlit as st
import tempfile

@st.cache(show_spinner=True)
def process_image(uploaded_file):
    tfile = tempfile.NamedTemporaryFile(delete=True)
    tfile.write(uploaded_file.read())
    image = np.asarray(Image.open(tfile))

    return image

def display_image(image):
    category = ['Animal', 'Flower', 'Bird']
    image_column, category_column = st.beta_columns(2)

    with image_column:
        st.image(image)
    with category_column:
        category_type = st.radio('Please select category: ', category)
        st.write('Image dimensions:')
        st.write(image.shape)
        st.write('Currently, the app gives predictions for a certain confidence threshold')
        st.write('You can enable the fun mode to give predictions in all cases')
        fun_mode = st.checkbox('Enable fun mode', value=False)

    return category_type.lower(), fun_mode
