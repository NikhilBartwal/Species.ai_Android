import numpy as np
import streamlit as st
import sqlite3 as sql

def get_cursor():
    db_path = 'Project/app/src/main/assets/databases/species.db'
    con = sql.connect(db_path)
    curr = con.cursor()
    return curr

def get_info(species_name, category):
    curr = get_cursor()
    if category=='flower':
        category = 'plant'
    query = 'SELECT * FROM ' + category + 'data WHERE name=?'
    data = curr.execute(query, (species_name, )).fetchall()[0]

    name = data[0]
    tag = data[1]
    description = data[2]
    url = data[3]
    taxon = data[4]

    info_dict = {
        'name': name,
        'tag': tag,
        'description': description,
        'url': url,
        'taxon': taxon
    }
    return info_dict

def display_info(info_dict):
    st.write('**Name:**', info_dict['name'])
    st.write('**Probability:**', str(info_dict['score']), '%')
    st.write('**Type:**', info_dict['tag'])
    if info_dict['taxon'] is not None:
        st.write('**Taxon:**', info_dict['taxon'])
