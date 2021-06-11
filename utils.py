from PIL import Image
import numpy as np
import streamlit as st
import tempfile

@st.cache(show_spinner=True)
def process_image(uploaded_file):
    tfile = tempfile.NamedTemporaryFile(delete=True)
    tfile.write(uploaded_file.read())

    image = Image.open(tfile)
    return image

def display_image(image):
    category = ['Animal', 'Flower', 'Bird']
    image_column, category_column = st.beta_columns(2)

    with image_column:
        st.image(image)
        predict_button = st.button('Predict!')
    with category_column:
        category_type = st.radio('Please select category: ', category)
        st.write('Image dimensions:')
        st.write(image.size)

    return category_type.lower(), predict_button

def display_sidebar(options):
    option = st.sidebar.selectbox('Please choose an option: ', options)
    return option
