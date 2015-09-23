package com.toyknight.aeii.utils;

import com.badlogic.gdx.files.FileHandle;
import com.toyknight.aeii.AEIIException;
import com.toyknight.aeii.entity.Unit;

import java.util.*;

/**
 * @author toyknight 4/3/2015.
 */
public class UnitFactory {

    private static Unit[] default_units;
    private static long current_code;

    private static int commander_index;
    private static int skeleton_index;
    private static int crystal_index;

    private UnitFactory() {
    }

    public static void loadUnitData() throws AEIIException {
        String unit_data_dir = "data/units/";
        FileHandle unit_config = FileProvider.getAssetsFile(unit_data_dir + "unit_config.dat");
        if (unit_config.exists()) {
            try {
                Scanner din = new Scanner(unit_config.read());
                int unit_count = din.nextInt();
                commander_index = din.nextInt();
                skeleton_index = din.nextInt();
                crystal_index = din.nextInt();
                din.close();
                default_units = new Unit[unit_count];
                for (int index = 0; index < unit_count; index++) {
                    FileHandle unit_data = FileProvider.getAssetsFile(unit_data_dir + "unit_" + index + ".dat");
                    if (unit_data.exists()) {
                        loadUnitData(unit_data, index);
                    } else {
                        throw new AEIIException("unit_" + index + ".dat not found!");
                    }
                }
            } catch (NoSuchElementException ex) {
                throw new AEIIException("tile_config.dat is broken!");
            }
        } else {
            throw new AEIIException("unit_config.dat not found!");
        }
    }

    private static void loadUnitData(FileHandle unit_data, int index) throws AEIIException {
        try {
            Scanner din = new Scanner(unit_data.read());
            int price = din.nextInt();
            int max_hp = din.nextInt();
            int mp = din.nextInt();
            int attack = din.nextInt();
            int physical_defence = din.nextInt();
            int magical_defence = din.nextInt();
            int atk_type = din.nextInt();
            int hp_growth = din.nextInt();
            int mp_growth = din.nextInt();
            int atk_growth = din.nextInt();
            int physical_defence_growth = din.nextInt();
            int magical_defence_growth = din.nextInt();
            int max_atk_rng = din.nextInt();
            int min_atk_rng = din.nextInt();
            int ability_count = din.nextInt();
            ArrayList<Integer> ability_list = new ArrayList<Integer>();
            for (int n = 0; n < ability_count; n++) {
                ability_list.add(din.nextInt());
            }
            din.close();
            Unit unit = new Unit(index);
            unit.setPrice(price);
            unit.setMaxHp(max_hp);
            unit.setMovementPoint(mp);
            unit.setAttack(attack);
            unit.setPhysicalDefence(physical_defence);
            unit.setMagicalDefence(magical_defence);
            unit.setAttackType(atk_type);
            unit.setHpGrowth(hp_growth);
            unit.setMovementGrowth(mp_growth);
            unit.setAttackGrowth(atk_growth);
            unit.setPhysicalDefenceGrowth(physical_defence_growth);
            unit.setMagicalDefenceGrowth(magical_defence_growth);
            unit.setMaxAttackRange(max_atk_rng);
            unit.setMinAttackRange(min_atk_rng);
            unit.setAbilities(ability_list);
            default_units[index] = unit;
        } catch (java.util.NoSuchElementException ex) {
            throw new AEIIException("bad unit data file!");
        }
    }

    public static int getCommanderIndex() {
        return commander_index;
    }

    public static int getSkeletonIndex() {
        return skeleton_index;
    }

    public static int getCrystalIndex() {
        return crystal_index;
    }

    public static int getUnitCount() {
        return default_units.length;
    }

    public static Unit getSample(int index) {
        return cloneUnit(default_units[index]);
    }

    public static Unit createUnit(int index, int team) {
        String unit_code = "#" + Long.toString(current_code++);
        return createUnit(index, team, unit_code);
    }

    public static Unit cloneUnit(Unit unit) {
        String unit_code = unit.getUnitCode();
        return new Unit(unit, unit_code);
    }

    public static Unit createUnit(int index, int team, String unit_code) {
        Unit unit = new Unit(default_units[index], unit_code);
        unit.setTeam(team);
        unit.setStandby(false);
        unit.setCurrentHp(unit.getMaxHp());
        unit.setCurrentMovementPoint(unit.getMovementPoint());
        return unit;
    }

    public static String getVerificationString() {
        String str = "";
        for (Unit unit : default_units) {
            str += unit.getVerificationString();
        }
        return str;
    }

}
